package com.example.demo;

import org.springframework.beans.factory.annotation.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.regions.Regions;


@Controller
public class ProfileController {
	
	@Value("${accessKey}")
	private String key;
	
	@Value("${accessPass}")
	private String pass;


	@GetMapping(value="/")
	public ModelAndView renderPage()
	{
		ModelAndView page=new ModelAndView();
		page.setViewName("index");
		return page;
		
	}


	
	@GetMapping(value="/upload")
	public ModelAndView renderProfilePage()
	{
		ModelAndView page=new ModelAndView();
		page.setViewName("profilePage");
		return page;
		
	}
	
	@PostMapping(value="/upload")
	public ModelAndView uploadFileToS3(@RequestParam("file")MultipartFile image)
	{
		ModelAndView page=new ModelAndView();
		try
		{
			BasicAWSCredentials credentials=new BasicAWSCredentials(key,
					pass);
			
			AmazonS3 s3client= AmazonS3ClientBuilder
					.standard()
					.withCredentials(new AWSStaticCredentialsProvider(credentials))
					.withRegion(Regions.US_EAST_2)
					.build();
			
			PutObjectRequest req=new PutObjectRequest("arandombucket",image.getOriginalFilename(),
					image.getInputStream(),new ObjectMetadata())
					.withCannedAcl(CannedAccessControlList.PublicRead);
			
			s3client.putObject(req);
			
			String imgSrc="http://arandombucket.s3.amazonaws.com/"+image.getOriginalFilename();
			
			page.addObject("imgSrc",imgSrc);
			
		page.setViewName("profilePage");
		}
		catch(Exception e)
		{
			page.setViewName("errorPage");
		}

		return page;
		
	}
	
}
